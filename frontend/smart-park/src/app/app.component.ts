import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { AppShellComponent } from './shell/app-shell.component';
import { GoogleMapsLoaderService } from './services/google-maps-loader.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, AppShellComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private googleMapsLoader: GoogleMapsLoaderService) {}

  ngOnInit(): void {
    this.googleMapsLoader.loadGoogleMapsScript().catch(err => {
      console.error('Failed to load Google Maps:', err);
    });
  }
}
