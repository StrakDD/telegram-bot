package org.test.task.telegrambotcryptocurrency.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MexcCryptocurrency {

    List<MexcTokenDetails> cryptocurrency;
}
