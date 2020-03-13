package io.github.agenttroll.ptr.scene;

// End scene setup used to tell the player if they won
// or lost, at least for single players.
// NOT intended to be used for two players!!
public class EndScene extends StaticImageScene {
    public EndScene(boolean win) {
        super(win ? "GameEnd/Win.png" : "GameEnd/Lose.png");
    }
}
