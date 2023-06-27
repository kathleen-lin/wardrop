import { Component, OnInit } from '@angular/core';
import { ItemService } from '../item.service';
import { OOTDfiles } from '../model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-drive',
  templateUrl: './drive.component.html',
  styleUrls: ['./drive.component.css']
})
export class DriveComponent implements OnInit{

  user!: string
  ootdImages: string[] = []
  ootdFolderId!: string
  succesfullyLoaded: boolean = false;
  constructor (private itmSvc: ItemService, private router: Router) {}
  
  ngOnInit(): void {
    // @ts-ignore
    this.user = localStorage.getItem("user")
    this.itmSvc.getOOTDdrive(this.user)
      .then((result) => {
        const ootds = result as OOTDfiles;
        this.ootdImages = ootds.files;
        this.ootdFolderId = ootds.folderId;
        // console.log(this.ootdFolderId);
        // console.log(this.ootdImages);
        this.succesfullyLoaded = true;

      })
      .catch((err) => console.log(err))
  }

  getDriveImageURL(imageId: string): string {
    return `https://drive.google.com/uc?id=${imageId}`;
  }

  navigateToUploadOOTD(){
    this.router.navigate(['/drive/add', this.ootdFolderId])
  }
  
}
