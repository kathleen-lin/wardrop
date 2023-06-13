import { Component, OnInit } from '@angular/core';
import { ItemService } from '../item.service';
import { OOTDfiles } from '../model';

@Component({
  selector: 'app-drive',
  templateUrl: './drive.component.html',
  styleUrls: ['./drive.component.css']
})
export class DriveComponent implements OnInit{

  ootdImages: string[] = []
  ootdFolderId!: string

  constructor (private itmSvc: ItemService) {}
  
  ngOnInit(): void {
    this.itmSvc.getOOTDdrive()
      .then((result) => {
        const ootds = result as OOTDfiles;
        this.ootdImages = ootds.files;
        this.ootdFolderId = ootds.folderId;
        console.log(this.ootdFolderId);
        console.log(this.ootdImages);
      })
      .catch((err) => console.log(err))
  }

  getDriveImageURL(imageId: string): string {
    return `https://drive.google.com/uc?id=${imageId}`;
  }
  
}
