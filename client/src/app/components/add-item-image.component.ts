import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ItemService } from '../item.service';
import { Router } from '@angular/router';
import { ApiResponse } from '../model';
import { PhotoService } from '../photo.service';

@Component({
  selector: 'app-add-item-image',
  templateUrl: './add-item-image.component.html',
  styleUrls: ['./add-item-image.component.css']
})
export class AddItemImageComponent {

  imageForm! :FormGroup

  response!: ApiResponse

  @ViewChild('photo')
  photoFile!: ElementRef

  imageUrl = ""

  constructor(private fb: FormBuilder, private itmSvc: ItemService, private photoSvc: PhotoService, private router: Router) {}

  ngOnInit(): void {

    this.imageForm = this.fb.group({
      photo: this.fb.control(''),
    })
    

  }
  sendImage() {
    
    const formdata = new FormData()

    const file: File = this.photoFile.nativeElement.files[0];
    const fileName: string = file.name;

    formdata.set('photo', file)
    this.itmSvc.uploadImage(formdata)
      .then((response) => {console.log(response)})
      .catch((err) => console.log(err));
    this.router.navigate(['/analyse', fileName])

  }


}
