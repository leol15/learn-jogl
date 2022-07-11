package com.play.app.ui.property;

import com.play.app.basics.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StringProperty {
    private String value;
    public final Event<StringProperty> updateEvent = new Event<StringProperty>(this);
}
