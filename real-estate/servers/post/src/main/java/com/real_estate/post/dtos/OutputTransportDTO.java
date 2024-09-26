package com.real_estate.post.dtos;

import com.real_estate.post.utils.TransportActionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutputTransportDTO {

    private TransportActionEnum action;

    private Object object;
}