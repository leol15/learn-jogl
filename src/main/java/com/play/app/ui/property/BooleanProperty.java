package com.play.app.ui.property;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class BooleanProperty {
    private boolean value;

    // weird, @Getter does not work
    public boolean getValue() {
        return value;
    }
}
