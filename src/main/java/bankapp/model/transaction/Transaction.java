package bankapp.model.transaction;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class Transaction {

    private int senderId;
    private int receiverId;
    private double amount;
    private LocalDateTime timestamp;

    public Transaction() { }
    public Transaction(int senderId , int receiverId , double amount , LocalDateTime timestamp){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

}
