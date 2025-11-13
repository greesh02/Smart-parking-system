export interface Coordinates {
  latitude: number;
  longitude: number;
}

export interface SlotCounts {
  car?: number;
  motorbike?: number;
  bus?: number;
  [key: string]: number | undefined;
}

export interface SlotInfo {
  availableSlotsCount: SlotCounts;
  occupiedSlotsCount: SlotCounts;
}

export interface LastUpdated {
  cameraService?: string;
  aiService?: string;
}

export interface ParkingLot {
  lotId: string;
  position: { x: number; y: number };
  streetAddress?: string;
  landmark?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
  imageUrlOriginal?: string;
  imageUrlProcessed?: string;
  slotInfo: SlotInfo;
  aiDescription?: string;

  lastUpdated?: LastUpdated;
}

export interface ParkingLotItem {
  parkingLot: ParkingLot;
  distanceMatrixResponseItem?: {
    distanceMeters?: number;
    duration?: string;
  };
}

export interface ParkingApiResponse {
  parkingLots: ParkingLotItem[];
  coordinates?: Coordinates;
}
