package com.awbd.pawsy.pet.specification;

import org.springframework.data.jpa.domain.Specification;
import com.awbd.pawsy.pet.model.Shelter;

public class ShelterSpecifications {
    public static Specification<Shelter> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Shelter> locationContains(String location) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }
}
