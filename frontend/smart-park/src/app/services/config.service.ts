import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface ApiConfig {
  apiBase: string;
  endpoints: { [k: string]: string };
}

export interface VehicleType {
  key: string;
  value: string;
}

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private apiCfg?: ApiConfig;
  private vehicleCfg?: VehicleType[];

  constructor(private http: HttpClient) {}

  async loadApiConfig(): Promise<ApiConfig> {
    if (this.apiCfg) return this.apiCfg;
    this.apiCfg = await this.http
      .get<ApiConfig>('./assets/api-config.json')
      .toPromise();
    return this.apiCfg!;
  }

  async loadVehicleTypes(): Promise<VehicleType[]> {
    if (this.vehicleCfg) return this.vehicleCfg;
    const res = await this.http
      .get<{ vehicleTypes: VehicleType[] }>('./assets/vehicle-types.json')
      .toPromise();
    this.vehicleCfg = res?.vehicleTypes;
    return this.vehicleCfg!;
  }

  get apiBase() {
    if (!this.apiCfg) throw new Error('API config not loaded');
    return this.apiCfg.apiBase;
  }

  get endpoints() {
    if (!this.apiCfg) throw new Error('API config not loaded');
    return this.apiCfg.endpoints;
  }

  get vehicleTypes() {
    if (!this.vehicleCfg) throw new Error('Vehicle types not loaded');
    return this.vehicleCfg;
  }
}
