package com.tianlun.ppmtool.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectNotFoundExceptionResponse {

    private String projectNotFound;
}
