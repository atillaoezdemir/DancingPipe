import {TempoPipe} from './tempo-pipe.pipe';
import {TempoLabels} from '../models/tempo-labels';

describe('TempoPipe', () => {
  let pipe: TempoPipe;

  beforeEach(() => {
    pipe = new TempoPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });


  it('should transform TempoLabels.VERY_SLOW to "Very Slow"', () => {
    expect(pipe.transform(TempoLabels.VERY_SLOW)).toBe('Very Slow');
  });

  it('should transform TempoLabels.SLOW to "Slow"', () => {
    expect(pipe.transform(TempoLabels.SLOW)).toBe('Slow');
  });

  it('should transform TempoLabels.NORMAL to "Normal"', () => {
    expect(pipe.transform(TempoLabels.NORMAL)).toBe('Normal');
  });

  it('should transform TempoLabels.FAST to "Fast"', () => {
    expect(pipe.transform(TempoLabels.FAST)).toBe('Fast');
  });

  it('should transform TempoLabels.VERY_FAST to "Very fast"', () => {
    expect(pipe.transform(TempoLabels.VERY_FAST)).toBe('Very fast');
  });
});
