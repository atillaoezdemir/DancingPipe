export interface WebClientDTO {
  keyboardsInUse: number;
  maxAvailableKeyboards: number;
  currentTempo: number;
  command: string;
  wasCommandExecuted: boolean;
  consumerConnected: boolean;
  startCommandReceived: boolean;
  barLength: number;
  title: string;
  composerName: string;
}
