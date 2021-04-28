package com.demo.Infrastructure;

import com.demo.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

// 代码在Infrastructure层
@Repository // Spring的注解
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderDAO dao; // 具体的DAO接口
    private final OrderDataConverter converter; // 转化器

    public OrderRepositoryImpl(OrderDAO dao) {
        this.dao = dao;
        this.converter = OrderDataConverter.INSTANCE;
    }

    @Override
    public Order find(OrderId orderId) {
        OrderDO orderDO = dao.findById(orderId.getValue());
        return converter.fromData(orderDO);
    }

    @Override
    public void remove(Order aggregate) {
        OrderDO orderDO = converter.toData(aggregate);
        dao.delete(orderDO);
    }

    @Override
    public void save(Order aggregate) {
        if (aggregate.getId() != null && aggregate.getId().getValue() > 0) {
            // update
            OrderDO orderDO = converter.toData(aggregate);
            dao.update(orderDO);
        } else {
            // insert
            OrderDO orderDO = converter.toData(aggregate);
            dao.insert(orderDO);
            aggregate.setId(converter.fromData(orderDO).getId());
        }
    }

    @Override
    public Page<Order> query(OrderQuery query) {
        List<OrderDO> orderDOS = dao.queryPaged(query);
        long count = dao.count(query);
        List<Order> result = orderDOS.stream().map(converter::fromData).collect(Collectors.toList());
        return Page.with(result, query, count);
    }

    @Override
    public Order findInStore(OrderId id, StoreId storeId) {
        OrderDO orderDO = dao.findInStore(id.getValue(), storeId.getValue());
        return converter.fromData(orderDO);
    }

}