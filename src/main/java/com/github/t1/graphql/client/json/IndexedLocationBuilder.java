package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;

import java.util.concurrent.atomic.AtomicInteger;

class IndexedLocationBuilder {
    private final TypeInfo itemType;
    private final String baseDescription;
    private final AtomicInteger index = new AtomicInteger();

    IndexedLocationBuilder(Location location) {
        this.itemType = location.getType().getItemType();
        this.baseDescription = location.getDescription();
    }

    public Location nextLocation() {
        return new Location(itemType, baseDescription + "[" + index.getAndIncrement() + "]");
    }
}
