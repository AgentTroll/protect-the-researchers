package io.github.agenttroll.ptr.scene;

public interface Scene {
    void resize(int width, int height);

    void render();

    void dispose();
}
