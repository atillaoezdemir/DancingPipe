import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of} from 'rxjs';
import {SseService} from '../services/sse.service';
import {WebClientDTO} from '../models/web-client-dto';
import {NumberDisplayComponent} from './number-display.component';
import {MatButtonModule} from '@angular/material/button';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

describe('NumberDisplayComponent', () => {
  let component: NumberDisplayComponent;
  let fixture: ComponentFixture<NumberDisplayComponent>;
  let sseService: jasmine.SpyObj<SseService>;
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    const sseServiceSpy = jasmine.createSpyObj('SseService', ['getWebClientData']);

    await TestBed.configureTestingModule({
      imports: [
        NumberDisplayComponent,
        MatButtonModule
      ],
      providers: [
        {provide: SseService, useValue: sseServiceSpy},
        provideHttpClientTesting()
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NumberDisplayComponent);
    component = fixture.componentInstance;
    sseService = TestBed.inject(SseService) as jasmine.SpyObj<SseService>;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with no data', () => {
    sseService.getWebClientData.and.returnValue(of());
    fixture.detectChanges();

    expect(component.webClientData.length).toBe(0);
    expect(component.displayedData.length).toBe(0);
  });

  it('should handle new data from SSE service', () => {
    const mockData: WebClientDTO = {
      keyboardsInUse: 1,
      maxAvailableKeyboards: 10,
      currentTempo: 120,
      command: 'start',
      wasCommandExecuted: true,
      consumerConnected: true,
      startCommandReceived: true,
      barLength: 4,
      title: 'Test Title',
      composerName: 'Test Composer'
    };
    sseService.getWebClientData.and.returnValue(of(mockData));

    fixture.detectChanges();

    expect(component.webClientData.length).toBe(1);
    expect(component.webClientData[0]).toEqual(mockData);
    expect(component.displayedData.length).toBe(1);
  });

  it('should not add data if keyboardsInUse is -1', () => {
    const mockData: WebClientDTO = {
      keyboardsInUse: -1,
      maxAvailableKeyboards: 10,
      currentTempo: 120,
      command: 'start',
      wasCommandExecuted: true,
      consumerConnected: true,
      startCommandReceived: true,
      barLength: 4,
      title: 'Test Title',
      composerName: 'Test Composer'
    };
    sseService.getWebClientData.and.returnValue(of(mockData));

    fixture.detectChanges();

    expect(component.webClientData.length).toBe(0);
    expect(component.displayedData.length).toBe(0);
  });

  it('should toggle view correctly', () => {
    component.showAll = false;
    component.webClientData = Array.from({length: 15}, (_, i) => ({
      keyboardsInUse: i,
      maxAvailableKeyboards: 10,
      currentTempo: 120 + i,
      command: 'start',
      wasCommandExecuted: true,
      consumerConnected: true,
      startCommandReceived: true,
      barLength: 4,
      title: `Test Title ${i}`,
      composerName: `Test Composer ${i}`
    }));

    component.toggleView();
    expect(component.showAll).toBeTrue();
    expect(component.displayedData.length).toBe(15);

    component.toggleView();
    expect(component.showAll).toBeFalse();
    expect(component.displayedData.length).toBe(10);
  });

  it('should clear data correctly', () => {
    component.webClientData = [{
      keyboardsInUse: 1,
      maxAvailableKeyboards: 10,
      currentTempo: 120,
      command: 'start',
      wasCommandExecuted: true,
      consumerConnected: true,
      startCommandReceived: true,
      barLength: 4,
      title: 'Test Title',
      composerName: 'Test Composer'
    }];
    component.displayedData = [{
      keyboardsInUse: 1,
      maxAvailableKeyboards: 10,
      currentTempo: 120,
      command: 'start',
      wasCommandExecuted: true,
      consumerConnected: true,
      startCommandReceived: true,
      barLength: 4,
      title: 'Test Title',
      composerName: 'Test Composer'
    }];

    component.clearData();

    expect(component.webClientData.length).toBe(0);
    expect(component.displayedData.length).toBe(0);
  });


});
