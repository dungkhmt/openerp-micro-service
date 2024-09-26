package openerp.notification.dto.rabbitMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    private InAppChannel inApp;

    private EmailChannel email;
}
