package com.demo.domain.base;

// 聚合根的Marker接口
public interface Aggregate<ID extends Identifier> extends Entity<ID> {

}
