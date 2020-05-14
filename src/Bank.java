import java.io.Serializable;

public class Bank implements Serializable{
    private int bankBalance;

    public Bank(int amount) {
        bankBalance = amount;
    }

        public void bet(GuessingGameShowClient.GuessingGameShow_PlayerThread_withBank p) {
            int amount;
            double percentageBet = (double) p.getPlayersBet() / p.getPlayersBank();
            if (p.getGuessNumber() == p.getActualNumber()) {
                amount = (int) (bankBalance * percentageBet);
                p.addToPlayersBank(amount);
                this.bankBalance -= amount;
            } else {
                amount = (int) (p.getPlayersBank() * percentageBet);
                p.subtractFromPlayersBank(amount);
                this.bankBalance += amount;
            }
        } // bet
} // Bank