import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ParkingService } from '../../services/parking.service';
import { ResultPageData } from '../../services/result-page-data.service';
import { ConfigService } from '../../services/config.service';

@Component({
  selector: 'app-search-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-page.component.html',
  styleUrls: ['./search-page.component.css']
})
export class SearchPageComponent {
  address = '';
  radius = 5;
  vehicleType = 'CAR';
  
  busy = false;

  vehicleTypes: { key: string; value: string }[] = [];


  async ngOnInit() {
    await this.cfg.loadVehicleTypes();
    this.vehicleTypes = this.cfg.vehicleTypes;
    this.vehicleType = this.vehicleTypes[0].key;
  }

  constructor(private cfg: ConfigService,private svc: ParkingService, private router: Router,private resultPageDataService: ResultPageData) {}

  async findByAddress() {
    this.busy = true;
    try {
      const resp = await this.svc.findByAddress(this.address, this.radius, this.vehicleType);
      
      this.resultPageDataService.setData(resp);
      this.resultPageDataService.setVehicleType(this.vehicleTypes.find(vt => vt.key === this.vehicleType)?.value || 'car');
      this.resultPageDataService.setSearchInfo('address', this.address);
      console.log(this.resultPageDataService.getVehicleType(),"vehicle type set in service");
      this.router.navigate(['/results']);
    } finally {
      this.busy = false;
    }
  }

  async findNearMe() {
    this.busy = true;
    navigator.geolocation.getCurrentPosition(async pos => {
      try {
        const { latitude, longitude } = pos.coords;
        const resp = await this.svc.findByLatLong(latitude, longitude, this.radius, this.vehicleType);
        if (resp && !resp.coordinates) resp.coordinates = { latitude, longitude };
        this.resultPageDataService.setData(resp);
        this.resultPageDataService.setVehicleType(this.vehicleTypes.find(vt => vt.key === this.vehicleType)?.value || 'car');
        this.resultPageDataService.setSearchInfo('current-location', '');
      console.log(this.resultPageDataService.getVehicleType(),"vehicle type set in service");

        this.router.navigate(['/results']);
      } finally {
        this.busy = false;
      }
    }, err => {
      alert(err.message);
      this.busy = false;
    });
  }
}
