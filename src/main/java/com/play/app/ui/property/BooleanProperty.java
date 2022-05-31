package com.play.app.ui.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
