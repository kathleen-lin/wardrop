import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {

  private imageUrl: string = '';

  setImageUrl(url: string): void {
    this.imageUrl = url;
  }

  getImageUrl(): string {
    return this.imageUrl;
  }
}
