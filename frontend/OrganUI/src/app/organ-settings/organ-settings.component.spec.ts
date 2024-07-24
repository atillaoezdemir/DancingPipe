import {ComponentFixture, TestBed} from '@angular/core/testing';
import {NgZone} from '@angular/core';
import {of, Subscription} from 'rxjs';
import {SseService} from '../services/sse.service';
import {WebClientDTO} from '../models/web-client-dto';
import {OrganSettingsComponent} from './organ-settings.component';
import {MatSliderModule} from '@angular/material/slider';
import {FormsModule} from '@angular/forms';
import {MatDividerModule} from '@angular/material/divider';
import {TempoPipe} from '../pipes/tempo-pipe.pipe';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

describe('OrganSettingsComponent', () => {
  let component: OrganSettingsComponent;
  let fixture: ComponentFixture<OrganSettingsComponent>;
  let sseService: jasmine.SpyObj<SseService>;
  let ngZone: NgZone;
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    const sseServiceSpy = jasmine.createSpyObj('SseService', ['getWebClientData']);

    await TestBed.configureTestingModule({
      imports: [
        OrganSettingsComponent,
        MatSliderModule,
        FormsModule,
        MatDividerModule,
        TempoPipe
      ],
      providers: [
        {provide: SseService, useValue: sseServiceSpy},
        provideHttpClientTesting()
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(OrganSettingsComponent);
    component = fixture.componentInstance;
    sseService = TestBed.inject(SseService) as jasmine.SpyObj<SseService>;
    ngZone = TestBed.inject(NgZone);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    if (component.subscription) {
      component.subscription.unsubscribe();
    }
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    sseService.getWebClientData.and.returnValue(of());
    fixture.detectChanges();

    expect(component.webClientData).toBeUndefined();
    expect(component.consumerIsConnected).toBeFalse();
    expect(component.startCommandReceived).toBeFalse();
    expect(component.barLength).toBe(-1);
    expect(component.title).toBe('stopped');
    expect(component.composerName).toBe('stopped');
  });

  it('should handle new data from SSE service', () => {
    const mockData: WebClientDTO = {
      keyboardsInUse: 2,
      maxAvailableKeyboards: 4,
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

    ngZone.run(() => {
      expect(component.webClientData).toEqual(mockData);
      expect(component.consumerIsConnected).toBeTrue();
      expect(component.selectedTempoLabel).toBe(120);
      expect(component.startCommandReceived).toBeTrue();
      expect(component.barLength).toBe(4);
      expect(component.title).toBe('Test Title');
      expect(component.composerName).toBe('Test Composer');
    });
  });

  it('should update keyboard states correctly', () => {
    const mockData: WebClientDTO = {
      keyboardsInUse: 3,
      maxAvailableKeyboards: 5,
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

    ngZone.run(() => {
      component.updateKeyboards();
      expect(component.keyboards).toEqual(['active', 'active', 'active', 'inactive', 'inactive']);
    });
  });

  it('should return correct keyboard image', () => {
    component.keyboards = ['active', 'inactive', 'disabled'];
    expect(component.getKeyboardImage(0)).toBe('active keyboard.png');
    expect(component.getKeyboardImage(1)).toBe('inactive keyboard.png');
    expect(component.getKeyboardImage(2)).toBe('disabled keyboard.png');
  });

  it('should return correct keyboard name', () => {
    expect(component.getKeyboardName(0)).toBe('Choir');
    expect(component.getKeyboardName(1)).toBe('Great');
    expect(component.getKeyboardName(2)).toBe('Swell');
    expect(component.getKeyboardName(3)).toBe('Solo/Echo');
    expect(component.getKeyboardName(4)).toBe('Pedal');
    expect(component.getKeyboardName(5)).toBe('Invalid index');
  });


  it('should clean up subscription on destroy', () => {
    component.ngOnDestroy();
    if (component.subscription instanceof Subscription) {
      expect(component.subscription.closed).toBeTrue();
    }
  });
});
