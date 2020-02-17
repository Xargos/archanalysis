package pl.demo.domain.whatever;

import lombok.RequiredArgsConstructor;
import pl.demo.TopLevel;
import pl.demo.infrastructure.SomeRepository;

public class WhatDomain {
    private SomeRepository someRepository;

    @RequiredArgsConstructor
    public static class InnerWhatDomain {
        private final TopLevel topLevel;
    }
}
