package com.acme.pulsar.model;

public record UserSignup(SignupTier signupTier, String firstName, String lastName, String email, long signupTimestamp) {
}
