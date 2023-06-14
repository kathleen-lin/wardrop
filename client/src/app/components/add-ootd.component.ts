import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemService } from '../item.service';
import { PhotoService } from '../photo.service';

@Component({
  selector: 'app-add-ootd',
  templateUrl: './add-ootd.component.html',
  styleUrls: ['./add-ootd.component.css']
})
export class AddOOTDComponent {
  
  imageForm! :FormGroup
  ootdFolerId!: string
  @ViewChild('photo')
  photoFile!: ElementRef

  user!: string

  constructor(private fb: FormBuilder, private itmSvc: ItemService, private photoSvc: PhotoService, private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {

    this.imageForm = this.fb.group({
      photo: this.fb.control(''),
    })

    this.activatedRoute.params.subscribe(
      async (params) => {
        this.ootdFolerId = params['folderId']; 
        console.log(this.ootdFolerId)
      })

  }
  sendImage() {
    
    const formdata = new FormData()
    // @ts-ignore
    this.user = localStorage.getItem("user")

    const file: File = this.photoFile.nativeElement.files[0];
    formdata.set('image', file);
    formdata.set('user', this.user);
    formdata.set('parentFolderId', this.ootdFolerId);

    
    this.itmSvc.uploadOOTD(formdata)
      .then((response) => {
        console.log(response);
        this.router.navigate(['/drive'])})
      .catch((err) => console.log(err));
    

  }

}
