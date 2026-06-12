package io.github.Novma.duels.duel;

import org.bukkit.entity.Player;

public class Duel {
    private final Player player1;
    private Player player2;
    private final int prize;

    public Duel(Player player1, Player player2, int prize) {
        this.player1 = player1;
        this.player2 = player2;
        this.prize = prize;
    }

    public void accept(Player player) {
        this.player2 = player;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public int getPrize() {
        return prize;
    }
}
