package io.github.caojohnny.ptr.scene;

public interface Scene {
    void resize(int width, int height);

    void render();

    void dispose();
}
