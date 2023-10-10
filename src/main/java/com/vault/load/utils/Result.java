package com.vault.load.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    protected T data;
    protected List<ErrorResponse> errors;

    public Result(final T data) {
        this.data = data;
        this.errors = null;
    }

    public Result(final ErrorResponse error) {
        this.errors = Collections.singletonList(error);
        this.data = null;
    }

    public void addError(final ErrorResponse error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
    }
}

