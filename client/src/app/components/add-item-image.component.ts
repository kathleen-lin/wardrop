import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ItemService } from '../item.service';
import { Router } from '@angular/router';
import { PhotoService } from '../photo.service';

@Component({
  selector: 'app-add-item-image',
  templateUrl: './add-item-image.component.html',
  styleUrls: ['./add-item-image.component.css']
})
export class AddItemImageComponent {

  imageForm! :FormGroup

  @ViewChild('photo')
  photoFile!: ElementRef

  constructor(private fb: FormBuilder, private itmSvc: ItemService, private photoSvc: PhotoService, private router: Router) {}

  ngOnInit(): void {

    this.imageForm = this.fb.group({
      photo: this.fb.control(''),
    })
    

  }
  sendImage() {
    
    const formdata = new FormData()
    
    const file: File = this.photoFile.nativeElement.files[0];
    formdata.set('photo', file)

    const fileName: string = file.name;
    const imageUrl = "https://waredrop.sgp1.digitaloceanspaces.com/" + fileName
    this.photoSvc.setImageUrl(imageUrl)
    
    this.itmSvc.uploadImage(formdata)
      .then((response) => {
        console.log(response);
        this.router.navigate(['/analyse', fileName])})
      .catch((err) => console.log(err));
    

  }


}
