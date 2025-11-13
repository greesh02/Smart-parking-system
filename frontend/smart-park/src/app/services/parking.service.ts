import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ConfigService } from './config.service';
import { ParkingApiResponse } from '../models/parking.model';

@Injectable({ providedIn: 'root' })
export class ParkingService {
  constructor(private http: HttpClient, private cfg: ConfigService) {}

  private async url(key: string) {
    await this.cfg.loadApiConfig();
    return `${this.cfg.apiBase}${this.cfg.endpoints[key]}`;
  }

  async findByLatLong(lat: number, long: number, radius: number, vehicleType = 'CAR') {
    const url = await this.url('parkingLot');
    const params = new HttpParams()
      .set('lat', lat.toString())
      .set('long', long.toString())
      .set('radius', radius.toString())
      .set('vehicleType', vehicleType);
    return this.http.get<ParkingApiResponse>(url, { params }).toPromise();
  }

  async findByAddress(address: string, radius: number, vehicleType = 'CAR') {
    const url = await this.url('parkingLot');
    const params = new HttpParams()
      .set('address', address)
      .set('radius', radius.toString())
      .set('vehicleType', vehicleType);
    return this.http.get<ParkingApiResponse>(url, { params }).toPromise();
  }

  // async fetchImages(names: string[]) {
  //   const url = await this.url('parkingImages');
  //   let params = new HttpParams();
  //   names.forEach(n => params = params.append('image', n));
  //   return this.http.get<any>(url, { params }).toPromise();
  // }

  async getImageUrl(imageName: string): Promise<string> {
    const url = await this.url('parkingImages');
    return `${url}/${imageName}`;
  }
}
