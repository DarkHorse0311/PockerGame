export class GameRules {
  id: number;
  smallBlind: number;
  bigBlind: number;
  playDelay: number;
  startingChips: number;
  maxPlayerCount: number;

  static create() {
    return {
      id: 0,
      smallBlind: 10,
      bigBlind: 20,
      playDelay: 30,
      startingChips: 1000,
      maxPlayerCount: 6
    };
  }
}
