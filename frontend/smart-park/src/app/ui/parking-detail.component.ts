import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParkingLotItem } from '../models/parking.model';
import { ParkingService } from '../services/parking.service';

@Component({
  selector: 'app-parking-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './parking-detail.component.html',
  styleUrls: ['./parking-detail.component.css']
})
export class ParkingDetailComponent implements OnInit {
  @Input() item!: ParkingLotItem | null;
  @Output() close = new EventEmitter<void>();

  images: string[] = [];
  loadingStates: boolean[] = [];
  previewImage: string | null = null;

  constructor(private svc: ParkingService) {}

  async ngOnInit() {
    if (!this.item) return;

    const imgs: string[] = [];
    if (this.item.parkingLot.imageUrlOriginal)
      imgs.push(this.item.parkingLot.imageUrlOriginal);
    if (this.item.parkingLot.imageUrlProcessed)
      imgs.push(this.item.parkingLot.imageUrlProcessed);

    if (!imgs.length) return;

    this.loadingStates = new Array(imgs.length).fill(true);

    this.images = await Promise.all(
      imgs.map(f => this.svc.getImageUrl(f))
    );

    console.log(this.images, 'Resolved image URLs');
  }

  onImageLoad(index: number) {
    this.loadingStates[index] = false;
  }

  openPreview(img: string) {
    this.previewImage = img;
  }

  closePreview() {
    this.previewImage = null;
  }

  openMaps() {
    if (!this.item) return;
    const dest = `${this.item.parkingLot.position.y},${this.item.parkingLot.position.x}`;
    window.open(
      `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(
        dest
      )}&travelmode=driving`,
      '_blank'
    );
  }

  formatDistance(meters?: number): string {
    if (!meters) return '-';
    if (meters < 1000) return `${meters}m`;
    return `${(meters / 1000).toFixed(1)}km`;
  }
}
