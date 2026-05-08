package com.awbd.pawsy.pet.specification;

import org.springframework.data.jpa.domain.Specification;
import com.awbd.pawsy.pet.model.Pet;

public final class PetSpecifications {
    public static Specification<Pet> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Pet> hasSpecies(String species) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("species")), "%" + species.toLowerCase() + "%");
    }

    public static Specification<Pet> hasSex(String sex) {
        return (root, query, cb) ->
                cb.equal(root.get("sex"), "%s%s".formatted(sex.substring(0, 1).toUpperCase(), sex.substring(1).toLowerCase()));
    }

    public static Specification<Pet> hasShelter(Long shelterId) {
        return (root, query, cb) ->
                cb.equal(root.get("shelter").get("id"), shelterId);
    }
}
