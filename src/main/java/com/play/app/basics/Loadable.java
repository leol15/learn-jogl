package com.play.app.basics;

import java.io.IOException;

import com.play.app.utils.WorldSerializer;

public interface Loadable {
    public void load(WorldSerializer reader) throws IOException;
}
