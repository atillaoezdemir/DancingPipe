import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'commandDisplayName',
  standalone: true
})
export class CommandDisplayNamePipe implements PipeTransform {

  private commandMappings: { [key: string]: string } = {
    start: 'Start',
    stop: 'Stop',
    incrementKeyboards: 'Add One Manual',
    decrementKeyboards: 'Remove One Manual',
    minKeyboards: 'Use One Keyboard',
    maxKeyboards: 'Use All Keyboards',
    incrementTempo: 'Increase Tempo',
    decrementTempo: 'Decrease Tempo',
    defaultTempo: 'Reset to Default Tempo'
  };

  transform(command: string): string {
    return this.commandMappings[command] || command;
  }
}
