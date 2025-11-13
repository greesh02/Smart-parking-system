import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ResultsPageComponent } from './results-page.component';
import { ResultPageData } from '../../services/result-page-data.service';
import { ParkingLotItem } from '../../models/parking.model';

describe('ResultsPageComponent', () => {
  let component: ResultsPageComponent;
  let fixture: ComponentFixture<ResultsPageComponent>;
  let router: jasmine.SpyObj<Router>;
  let resultPageDataService: jasmine.SpyObj<ResultPageData>;

  const mockParkingLotItem: ParkingLotItem = {
    parkingLot: {
      lotId: 'test-lot-1',
      position: { x: 77.57, y: 12.96 },
      slotInfo: {
        availableSlotsCount: { car: 5, motorbike: 3 },
        occupiedSlotsCount: { car: 3, motorbike: 2 }
      }
    },
    distanceMatrixResponseItem: {
      distanceMeters: 1500,
      duration: '300s'
    }
  };

  beforeEach(async () => {
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const serviceSpy = jasmine.createSpyObj('ResultPageData', [
      'getData',
      'getVehicleType',
      'getSearchMethod',
      'getSearchAddress'
    ]);

    await TestBed.configureTestingModule({
      imports: [ResultsPageComponent],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: ResultPageData, useValue: serviceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ResultsPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    resultPageDataService = TestBed.inject(ResultPageData) as jasmine.SpyObj<ResultPageData>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should navigate to home if no data is available', () => {
      resultPageDataService.getData.and.returnValue(null);
      resultPageDataService.getVehicleType.and.returnValue('car');
      resultPageDataService.getSearchMethod.and.returnValue(null);
      resultPageDataService.getSearchAddress.and.returnValue('');

      component.ngOnInit();

      expect(router.navigate).toHaveBeenCalledWith(['/home']);
    });

    it('should load data and apply filters when data is available', () => {
      const mockData = {
        parkingLots: [mockParkingLotItem],
        coordinates: { latitude: 12.96, longitude: 77.57 }
      };

      resultPageDataService.getData.and.returnValue(mockData);
      resultPageDataService.getVehicleType.and.returnValue('car');
      resultPageDataService.getSearchMethod.and.returnValue('address');
      resultPageDataService.getSearchAddress.and.returnValue('Test Address');

      component.ngOnInit();

      expect(component.items().length).toBe(1);
      expect(component.center.lat).toBe(12.96);
      expect(component.center.lng).toBe(77.57);
      expect(component.vehicleType).toBe('car');
      expect(component.searchMethod).toBe('address');
      expect(component.searchAddress).toBe('Test Address');
    });
  });

  describe('applyFilters', () => {
    beforeEach(() => {
      component.items.set([
        mockParkingLotItem,
        {
          ...mockParkingLotItem,
          parkingLot: { ...mockParkingLotItem.parkingLot, lotId: 'test-lot-2' },
          distanceMatrixResponseItem: { distanceMeters: 500, duration: '120s' }
        }
      ]);
    });

    it('should filter by minimum free slots', () => {
      component.minFree = 4;
      component.vehicleType = 'car';

      component.applyFilters();

      expect(component.visible().length).toBe(1);
      expect(component.visible()[0].parkingLot.lotId).toBe('test-lot-1');
    });

    it('should sort by distance ascending', () => {
      component.sortBy = 'distance';
      component.sortDir = 'asc';

      component.applyFilters();

      expect(component.visible()[0].distanceMatrixResponseItem?.distanceMeters).toBe(500);
      expect(component.visible()[1].distanceMatrixResponseItem?.distanceMeters).toBe(1500);
    });

    it('should sort by distance descending', () => {
      component.sortBy = 'distance';
      component.sortDir = 'desc';

      component.applyFilters();

      expect(component.visible()[0].distanceMatrixResponseItem?.distanceMeters).toBe(1500);
      expect(component.visible()[1].distanceMatrixResponseItem?.distanceMeters).toBe(500);
    });

    it('should sort by available slots', () => {
      component.items.set([
        {
          ...mockParkingLotItem,
          parkingLot: {
            ...mockParkingLotItem.parkingLot,
            slotInfo: {
              availableSlotsCount: { car: 2 },
              occupiedSlotsCount: { car: 1 }
            }
          }
        },
        mockParkingLotItem
      ]);
      component.sortBy = 'available';
      component.sortDir = 'asc';
      component.vehicleType = 'car';

      component.applyFilters();

      expect(component.visible()[0].parkingLot.slotInfo.availableSlotsCount?.car).toBe(2);
      expect(component.visible()[1].parkingLot.slotInfo.availableSlotsCount?.car).toBe(5);
    });
  });

  describe('getAvailabilityText', () => {
    it('should return formatted availability text', () => {
      component.vehicleType = 'car';
      const result = component.getAvailabilityText(mockParkingLotItem);
      expect(result).toBe('5/8');
    });

    it('should return "-" when no slot info', () => {
      const itemWithoutSlots = {
        ...mockParkingLotItem,
        parkingLot: { ...mockParkingLotItem.parkingLot, slotInfo: undefined as any }
      };
      const result = component.getAvailabilityText(itemWithoutSlots);
      expect(result).toBe('-');
    });
  });

  describe('getAvailabilityBadgeColor', () => {
    it('should return success color for high availability', () => {
      component.vehicleType = 'car';
      const result = component.getAvailabilityBadgeColor(mockParkingLotItem);
      expect(result).toBe('var(--success-light)');
    });

    it('should return danger color for low availability', () => {
      component.vehicleType = 'car';
      const lowAvailabilityItem = {
        ...mockParkingLotItem,
        parkingLot: {
          ...mockParkingLotItem.parkingLot,
          slotInfo: {
            availableSlotsCount: { car: 1 },
            occupiedSlotsCount: { car: 9 }
          }
        }
      };
      const result = component.getAvailabilityBadgeColor(lowAvailabilityItem);
      expect(result).toBe('var(--danger-light)');
    });
  });

  describe('formatDistance', () => {
    it('should format meters correctly', () => {
      expect(component.formatDistance(500)).toBe('500m');
    });

    it('should format kilometers correctly', () => {
      expect(component.formatDistance(1500)).toBe('1.5km');
    });

    it('should return "-" for undefined', () => {
      expect(component.formatDistance(undefined)).toBe('-');
    });
  });

  describe('formatVehicleType', () => {
    it('should capitalize first letter', () => {
      expect(component.formatVehicleType('car')).toBe('Car');
      expect(component.formatVehicleType('motorbike')).toBe('Motorbike');
    });

    it('should return empty string for empty input', () => {
      expect(component.formatVehicleType('')).toBe('');
    });
  });

  describe('formatDuration', () => {
    it('should format seconds format correctly', () => {
      expect(component.formatDuration('300s')).toBe('5 min');
    });

    it('should format HH:MM:SS format correctly', () => {
      expect(component.formatDuration('0:05:30')).toBe('6 min');
    });

    it('should format MM:SS format correctly', () => {
      expect(component.formatDuration('5:30')).toBe('6 min');
    });

    it('should return "< 1 min" for very short durations', () => {
      expect(component.formatDuration('30s')).toBe('< 1 min');
    });

    it('should return "-" for undefined', () => {
      expect(component.formatDuration(undefined)).toBe('-');
    });
  });

  describe('select', () => {
    it('should set selected item', () => {
      component.select(mockParkingLotItem);
      expect(component.selected).toBe(mockParkingLotItem);
    });
  });

  describe('getMarkerIcon', () => {
    it('should create marker icon with correct number', () => {
      const icon = component.getMarkerIcon(0);
      expect(icon.url).toContain('data:image/svg+xml');
      expect(icon.scaledSize?.width).toBe(30);
      expect(icon.scaledSize?.height).toBe(30);
    });
  });
});

