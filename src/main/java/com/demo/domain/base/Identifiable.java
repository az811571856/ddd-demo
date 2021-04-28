package com.demo.domain.base;

public interface Identifiable<ID extends Identifier> {
    ID getId();
}
