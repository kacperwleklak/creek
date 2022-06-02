package pl.poznan.put.kacperwleklak.creek.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;

import java.io.Serializable;

@Data
public abstract class CreekMsg implements Serializable {


    public CreekMsg() {}
}
