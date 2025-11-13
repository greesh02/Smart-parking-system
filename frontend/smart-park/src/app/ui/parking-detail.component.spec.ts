import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ParkingDetailComponent } from './parking-detail.component';
import { ParkingService } from '../services/parking.service';
import { ParkingLotItem } from '../models/parking.model';

describe('ParkingDetailComponent', () => {
  let component: ParkingDetailComponent;
  let fixture: ComponentFixture<ParkingDetailComponent>;
  let parkingService: jasmine.SpyObj<ParkingService>;

  const mockParkingLotItem: ParkingLotItem = {
    parkingLot: {
      lotId: 'test-lot-1',
      position: { x: 77.57, y: 12.96 },
      streetAddress: '123 Test St',
      city: 'Test City',
      slotInfo: {
        availableSlotsCount: { car: 5 },
        occupiedSlotsCount: { car: 3 }
      },
      imageUrlOriginal: 'original.jpg',
      imageUrlProcessed: 'processed.jpg'
    },
    distanceMatrixResponseItem: {
      distanceMeters: 1500
    }
  };

  beforeEach(async () => {
    const parkingServiceSpy = jasmine.createSpyObj('ParkingService', ['getImageUrl']);

    await TestBed.configureTestingModule({
      imports: [ParkingDetailComponent],
      providers: [
        { provide: ParkingService, useValue: parkingServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ParkingDetailComponent);
    component = fixture.componentInstance;
    parkingService = TestBed.inject(ParkingService) as jasmine.SpyObj<ParkingService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load images when item has image URLs', async () => {
      component.item = mockParkingLotItem;
      parkingService.getImageUrl.and.returnValues(
        Promise.resolve('http://example.com/original.jpg'),
        Promise.resolve('http://example.com/processed.jpg')
      );

      await component.ngOnInit();

      expect(parkingService.getImageUrl).toHaveBeenCalledTimes(2);
      expect(component.images.length).toBe(2);
      expect(component.loadingStates.length).toBe(2);
      expect(component.loadingStates[0]).toBe(true);
    });

    it('should not load images when item is null', async () => {
      component.item = null;
      await component.ngOnInit();
      expect(parkingService.getImageUrl).not.toHaveBeenCalled();
    });

    it('should not load images when no image URLs', async () => {
      component.item = {
        ...mockParkingLotItem,
        parkingLot: {
          ...mockParkingLotItem.parkingLot,
          imageUrlOriginal: undefined,
          imageUrlProcessed: undefined
        }
      };
      await component.ngOnInit();
      expect(parkingService.getImageUrl).not.toHaveBeenCalled();
    });
  });

  describe('onImageLoad', () => {
    it('should set loading state to false', () => {
      component.loadingStates = [true, true];
      component.onImageLoad(0);
      expect(component.loadingStates[0]).toBe(false);
      expect(component.loadingStates[1]).toBe(true);
    });
  });

  describe('openPreview', () => {
    it('should set preview image', () => {
      const testImage = 'http://example.com/image.jpg';
      component.openPreview(testImage);
      expect(component.previewImage).toBe(testImage);
    });
  });

  describe('closePreview', () => {
    it('should clear preview image', () => {
      component.previewImage = 'http://example.com/image.jpg';
      component.closePreview();
      expect(component.previewImage).toBeNull();
    });
  });

  describe('openMaps', () => {
    it('should open Google Maps with correct destination', () => {
      component.item = mockParkingLotItem;
      spyOn(window, 'open');

      component.openMaps();

      expect(window.open).toHaveBeenCalledWith(
        jasmine.stringContaining('12.96,77.57'),
        '_blank'
      );
    });

    it('should not open maps when item is null', () => {
      component.item = null;
      spyOn(window, 'open');
      component.openMaps();
      expect(window.open).not.toHaveBeenCalled();
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
});

