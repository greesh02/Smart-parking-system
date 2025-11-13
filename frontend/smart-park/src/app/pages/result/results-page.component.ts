import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GoogleMapsModule } from '@angular/google-maps';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParkingLotItem } from '../../models/parking.model';
import { ParkingDetailComponent } from '../../ui/parking-detail.component';
import { ResultPageData } from '../../services/result-page-data.service';

@Component({
  selector: 'app-results-page',
  standalone: true,
  imports: [CommonModule, FormsModule, GoogleMapsModule, ParkingDetailComponent],
  templateUrl: './results-page.component.html',
  styleUrls: ['./results-page.component.css']
})
export class ResultsPageComponent implements OnInit {
  items = signal<ParkingLotItem[]>([]);
  visible = signal<ParkingLotItem[]>([]);
  selected: ParkingLotItem | null = null;

  sortBy: 'distance' | 'duration' | 'available' = 'distance';
  sortDir: 'asc' | 'desc' = 'asc';
  minFree = 0;
  vehicleType = 'car';

  center: google.maps.LatLngLiteral = { lat: 12.96, lng: 77.57 };
  zoom = 12;
  
  searchMethod: 'address' | 'current-location' | null = null;
  searchAddress: string = '';
  
  mapOptions: google.maps.MapOptions = {
    zoomControl: true,
    mapTypeControl: true,
    fullscreenControl: true,
    streetViewControl: true,
    disableDefaultUI: false,
    gestureHandling: 'cooperative',
    mapTypeId: 'roadmap',
    styles: []
  };

  constructor(private router: Router, private resultPageDataService: ResultPageData) {}

  ngOnInit(): void {
    const state = this.resultPageDataService.getData();
    this.vehicleType = this.resultPageDataService.getVehicleType();
    this.searchMethod = this.resultPageDataService.getSearchMethod();
    this.searchAddress = this.resultPageDataService.getSearchAddress();

    if (!state) {
      this.router.navigate(['/home']);
      return;
    }

    const resp = state as any;
    this.items.set(resp.parkingLots || []);
    if (resp.coordinates)
      this.center = {
        lat: resp.coordinates.latitude,
        lng: resp.coordinates.longitude
      };

    this.applyFilters();
  }

  applyFilters() {
    let list = [...this.items()];
    if (this.minFree > 0) {
      list = list.filter(
        it => (it.parkingLot?.slotInfo?.availableSlotsCount?.[this.vehicleType] ?? 0) >= this.minFree
      );
    }

    list.sort((a, b) => {
      let av: number;
      let bv: number;

      if (this.sortBy === 'distance') {
        av = a.distanceMatrixResponseItem?.distanceMeters ?? 0;
        bv = b.distanceMatrixResponseItem?.distanceMeters ?? 0;
      } else if (this.sortBy === 'duration') {
        av = this._dur(a.distanceMatrixResponseItem?.duration);
        bv = this._dur(b.distanceMatrixResponseItem?.duration);
      } else if (this.sortBy === 'available') {
        av = a.parkingLot?.slotInfo?.availableSlotsCount?.[this.vehicleType] ?? 0;
        bv = b.parkingLot?.slotInfo?.availableSlotsCount?.[this.vehicleType] ?? 0;
      } else {
        av = 0;
        bv = 0;
      }

      return this.sortDir === 'asc' ? av - bv : bv - av;
    });

    this.visible.set(list);
  }

  _dur(d?: string): number {
    if (!d) return 999999;
    const m = d.match(/^(\d+)s$/);
    if (m) return Number(m[1]);
    const parts = d.split(':').map(n => +n);
    if (parts.length === 3) return parts[0] * 3600 + parts[1] * 60 + parts[2];
    return 999999;
  }

  select(p: ParkingLotItem) {
    this.selected = p;
  }

  getAvailabilityText(it: ParkingLotItem): string {
    const slotInfo = it.parkingLot?.slotInfo;
    if (!slotInfo) return '-';

    const available = slotInfo.availableSlotsCount?.[this.vehicleType] ?? 0;
    const total =
      (available + (slotInfo.occupiedSlotsCount?.[this.vehicleType] ?? 0)) || 0;
    return total > 0 ? `${available}/${total}` : '-';
  }

  getAvailabilityColor(it: ParkingLotItem): string {
    const slotInfo = it.parkingLot?.slotInfo;
    if (!slotInfo) return '#ccc';

    const available = slotInfo.availableSlotsCount?.[this.vehicleType] ?? 0;
    const total =
      (available + (slotInfo.occupiedSlotsCount?.[this.vehicleType] ?? 0)) || 0;

    if (total === 0) return '#ccc';
    const ratio = available / total;

    const r = Math.round(255 * (1 - ratio));
    const g = Math.round(255 * ratio);
    return `rgb(${r},${g},0)`;
  }

  getAvailabilityBadgeColor(it: ParkingLotItem): string {
    const slotInfo = it.parkingLot?.slotInfo;
    if (!slotInfo) return 'var(--danger-light)';

    const available = slotInfo.availableSlotsCount?.[this.vehicleType] ?? 0;
    const total =
      (available + (slotInfo.occupiedSlotsCount?.[this.vehicleType] ?? 0)) || 0;

    if (total === 0) return 'var(--danger-light)';
    const ratio = available / total;

    if (ratio >= 0.5) return 'var(--success-light)';
    if (ratio >= 0.2) return 'var(--warning-light)';
    return 'var(--danger-light)';
  }

  formatDistance(meters?: number): string {
    if (!meters) return '-';
    if (meters < 1000) return `${meters}m`;
    return `${(meters / 1000).toFixed(1)}km`;
  }

  formatVehicleType(type: string): string {
    if (!type) return '';
    return type.charAt(0).toUpperCase() + type.slice(1);
  }

  formatDuration(duration?: string): string {
    if (!duration) return '-';
    
    let totalSeconds = 0;
    
    const secondsMatch = duration.match(/^(\d+)s$/);
    if (secondsMatch) {
      totalSeconds = parseInt(secondsMatch[1], 10);
    } else {
      const timeParts = duration.split(':').map(n => parseInt(n, 10));
      if (timeParts.length === 3) {
        totalSeconds = timeParts[0] * 3600 + timeParts[1] * 60 + timeParts[2];
      } else if (timeParts.length === 2) {
        totalSeconds = timeParts[0] * 60 + timeParts[1];
      } else {
        const numSeconds = parseInt(duration, 10);
        if (!isNaN(numSeconds)) {
          totalSeconds = numSeconds;
        } else {
          return duration;
        }
      }
    }
    
    const minutes = Math.round(totalSeconds / 60);
    
    if (minutes === 0) {
      return '< 1 min';
    } else if (minutes === 1) {
      return '1 min';
    } else {
      return `${minutes} min`;
    }
  }

  getMarkerIcon(index: number): google.maps.Icon {
    const number = index + 1;
    const color = "#4F46E5";
    const textColor = "#FFFFFF";

    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
        <circle cx="16" cy="16" r="14" fill="${color}" stroke="white" stroke-width="2"/>
        <text x="16" y="20" text-anchor="middle" font-size="12" font-weight="bold"
              fill="${textColor}" font-family="Poppins, sans-serif">${number}</text>
      </svg>
    `;

    return {
      url: 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(svg),
      scaledSize: new google.maps.Size(30, 30),
      anchor: new google.maps.Point(15, 15),
    };
  }
}
