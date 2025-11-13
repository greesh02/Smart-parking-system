import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SearchPageComponent } from './search-page.component';
import { ParkingService } from '../../services/parking.service';
import { ResultPageData } from '../../services/result-page-data.service';
import { ConfigService } from '../../services/config.service';

describe('SearchPageComponent', () => {
  let component: SearchPageComponent;
  let fixture: ComponentFixture<SearchPageComponent>;
  let parkingService: jasmine.SpyObj<ParkingService>;
  let router: jasmine.SpyObj<Router>;
  let resultPageDataService: jasmine.SpyObj<ResultPageData>;
  let configService: jasmine.SpyObj<ConfigService>;

  beforeEach(async () => {
    const parkingServiceSpy = jasmine.createSpyObj('ParkingService', ['findByAddress', 'findByLatLong']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const resultPageDataSpy = jasmine.createSpyObj('ResultPageData', [
      'setData',
      'setVehicleType',
      'setSearchInfo'
    ]);
    const configServiceSpy = jasmine.createSpyObj('ConfigService', ['loadVehicleTypes'], {
      vehicleTypes: [
        { key: 'CAR', value: 'car' },
        { key: 'MOTORBIKE', value: 'motorbike' },
        { key: 'BUS', value: 'bus' }
      ]
    });

    await TestBed.configureTestingModule({
      imports: [SearchPageComponent],
      providers: [
        { provide: ParkingService, useValue: parkingServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ResultPageData, useValue: resultPageDataSpy },
        { provide: ConfigService, useValue: configServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchPageComponent);
    component = fixture.componentInstance;
    parkingService = TestBed.inject(ParkingService) as jasmine.SpyObj<ParkingService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    resultPageDataService = TestBed.inject(ResultPageData) as jasmine.SpyObj<ResultPageData>;
    configService = TestBed.inject(ConfigService) as jasmine.SpyObj<ConfigService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load vehicle types and set default', async () => {
      configService.loadVehicleTypes.and.returnValue(Promise.resolve([
        { key: 'CAR', value: 'car' },
        { key: 'MOTORBIKE', value: 'motorbike' }
      ]));

      await component.ngOnInit();

      expect(configService.loadVehicleTypes).toHaveBeenCalled();
      expect(component.vehicleType).toBe('CAR');
    });
  });

  describe('findByAddress', () => {
    it('should search by address and navigate to results', async () => {
      const mockResponse = {
        parkingLots: [],
        coordinates: { latitude: 12.96, longitude: 77.57 }
      };

      component.address = 'Test Address';
      component.radius = 5;
      component.vehicleType = 'CAR';
      component.vehicleTypes = [
        { key: 'CAR', value: 'car' }
      ];

      parkingService.findByAddress.and.returnValue(Promise.resolve(mockResponse));

      await component.findByAddress();

      expect(parkingService.findByAddress).toHaveBeenCalledWith('Test Address', 5, 'CAR');
      expect(resultPageDataService.setData).toHaveBeenCalledWith(mockResponse);
      expect(resultPageDataService.setVehicleType).toHaveBeenCalledWith('car');
      expect(resultPageDataService.setSearchInfo).toHaveBeenCalledWith('address', 'Test Address');
      expect(router.navigate).toHaveBeenCalledWith(['/results']);
      expect(component.busy).toBe(false);
    });

    it('should set busy flag correctly', async () => {
      component.address = 'Test';
      component.vehicleTypes = [{ key: 'CAR', value: 'car' }];
      parkingService.findByAddress.and.returnValue(Promise.resolve({ parkingLots: [] }));

      const promise = component.findByAddress();
      expect(component.busy).toBe(true);

      await promise;
      expect(component.busy).toBe(false);
    });
  });

  describe('findNearMe', () => {
    it('should get current location and navigate to results', async () => {
      const mockResponse = {
        parkingLots: [],
        coordinates: { latitude: 12.96, longitude: 77.57 }
      };

      component.radius = 5;
      component.vehicleType = 'CAR';
      component.vehicleTypes = [{ key: 'CAR', value: 'car' }];

      const mockPosition = {
        coords: {
          latitude: 12.96,
          longitude: 77.57
        }
      };

      parkingService.findByLatLong.and.returnValue(Promise.resolve(mockResponse));

      spyOn(navigator.geolocation, 'getCurrentPosition').and.callFake((success: any) => {
        success(mockPosition);
      });

      await component.findNearMe();

      expect(parkingService.findByLatLong).toHaveBeenCalledWith(12.96, 77.57, 5, 'CAR');
      expect(resultPageDataService.setData).toHaveBeenCalled();
      expect(resultPageDataService.setSearchInfo).toHaveBeenCalledWith('current-location', '');
      expect(router.navigate).toHaveBeenCalledWith(['/results']);
      expect(component.busy).toBe(false);
    });

    it('should handle geolocation error', async () => {
      const mockError = { message: 'Permission denied' };
      spyOn(window, 'alert');
      spyOn(navigator.geolocation, 'getCurrentPosition').and.callFake((success: any, error: any) => {
        error(mockError);
      });

      await component.findNearMe();

      expect(window.alert).toHaveBeenCalledWith('Permission denied');
      expect(component.busy).toBe(false);
    });
  });
});

