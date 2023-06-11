import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {

  private itemDescription: string = '';
  private imageUrl: string = '';

  setItemDescription(description: string): void {
    this.itemDescription = description;
  }

  getItemDescription(): string {
    return this.itemDescription;
  }

  setImageUrl (url: string): void {
    this.imageUrl = url;
  }

  getImageUrl(): string {
    return this.imageUrl;
  }
}
