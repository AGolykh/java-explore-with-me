package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAdminUpdateRequestDto extends EventUpdateRequestDto {
    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT
    }
}
