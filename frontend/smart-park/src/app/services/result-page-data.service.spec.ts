import { TestBed } from '@angular/core/testing';
import { ResultPageData } from './result-page-data.service';

describe('ResultPageData', () => {
  let service: ResultPageData;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResultPageData);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Data Management', () => {
    it('should set and get data', () => {
      const testData = { parkingLots: [], coordinates: { latitude: 12.96, longitude: 77.57 } };
      service.setData(testData);
      expect(service.getData()).toEqual(testData);
    });

    it('should clear data', () => {
      service.setData({ parkingLots: [] });
      service.clear();
      expect(service.getData()).toBeNull();
    });
  });

  describe('Vehicle Type Management', () => {
    it('should set and get vehicle type', () => {
      service.setVehicleType('motorbike');
      expect(service.getVehicleType()).toBe('motorbike');
    });

    it('should default to car', () => {
      expect(service.getVehicleType()).toBe('car');
    });
  });

  describe('Search Info Management', () => {
    it('should set and get search method for address', () => {
      service.setSearchInfo('address', '123 Main St');
      expect(service.getSearchMethod()).toBe('address');
      expect(service.getSearchAddress()).toBe('123 Main St');
    });

    it('should set and get search method for current location', () => {
      service.setSearchInfo('current-location');
      expect(service.getSearchMethod()).toBe('current-location');
      expect(service.getSearchAddress()).toBe('');
    });

    it('should clear search info when clearing data', () => {
      service.setSearchInfo('address', 'Test Address');
      service.clear();
      expect(service.getSearchMethod()).toBeNull();
      expect(service.getSearchAddress()).toBe('');
    });
  });
});

