import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ResultPageData {
  private _data: any = null;
  private _vehicleType: string = 'car';
  private _searchMethod: 'address' | 'current-location' | null = null;
  private _searchAddress: string = '';

  setData(data: any) {
    this._data = data;
  }

  getData() {
    return this._data;
  }

  clear() {
    this._data = null;
    this._searchMethod = null;
    this._searchAddress = '';
  }

  setVehicleType(type: string) {
    this._vehicleType = type;
  } 
  
  getVehicleType() {
    return this._vehicleType;
  }

  setSearchInfo(method: 'address' | 'current-location', address: string = '') {
    this._searchMethod = method;
    this._searchAddress = address;
  }

  getSearchMethod() {
    return this._searchMethod;
  }

  getSearchAddress() {
    return this._searchAddress;
  }
}
