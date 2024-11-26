package openerp.openerpresourceserver.masterconfig.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeLastTimeProcessId {
    private String tableName;
    private String module;
}
