import { Pipe, PipeTransform } from '@angular/core';
import { TempoLabels } from '../model/enums';

@Pipe({ name: 'tempo', standalone: true })
export class TempoPipe implements PipeTransform {
  transform(tempo: TempoLabels) {
    switch (tempo) {
      case TempoLabels.STOPPED:
        return 'Stopped';
      case TempoLabels.VERY_SLOW:
        return 'Very Slow';
      case TempoLabels.SLOW:
        return 'Slow';
      case TempoLabels.NORMAL:
        return 'Normal';
      case TempoLabels.FAST:
        return 'Fast';
      case TempoLabels.VERY_FAST:
        return 'Very fast';
    }
  }
}
