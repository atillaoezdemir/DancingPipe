import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganSettingsComponent } from './organ-settings.component';

describe('OrganSettingsComponent', () => {
  let component: OrganSettingsComponent;
  let fixture: ComponentFixture<OrganSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrganSettingsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OrganSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
