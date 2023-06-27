import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ItemService } from '../item.service';
import { Observable, of } from 'rxjs';
import { PhotoService } from '../photo.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-item',
  templateUrl: './add-item.component.html',
  styleUrls: ['./add-item.component.css']
})
export class AddItemComponent implements OnInit, AfterViewInit {
 
  user!: string

  addForm!: FormGroup 

  @ViewChild('photo')
  photoFile!: ElementRef

  itmDescription = ''

  photoUrl! :string

  constructor(private fb: FormBuilder, private itmSvc: ItemService, private photoSvc: PhotoService, private router: Router) {}
  

  ngOnInit(): void {
    this.photoUrl = this.photoSvc.getImageUrl(); 
    // console.log(this.photoUrl);
    // @ts-ignore
    this.user = localStorage.getItem("user")
    this.itmDescription = this.photoSvc.getItemDescription();
    this.addForm = this.fb.group({
      description: this.fb.control(''),
      price: this.fb.control(''),
      purchaseOn: this.fb.control(''),
      timeWorn: this.fb.control(''),
      category: this.fb.control(''),
      })
  }

  ngAfterViewInit(): void {
      this.addForm = this.createForm(this.itmDescription);
  }

  
  
  // post the data to backend, then persist into database
  processForm() {
      // @ts-ignore
      const formdata = new FormData()
      formdata.set('photoUrl', this.photoUrl)
      formdata.set('description', this.addForm.get('description')?.value)
      formdata.set('price', this.addForm.get('price')?.value)
      formdata.set('purchaseOn', this.addForm.get('purchaseOn')?.value)
      formdata.set('timeWorn', this.addForm.get('timeWorn')?.value)
      formdata.set('category', this.addForm.get('category')?.value)
      formdata.set('userName', this.user)
      // console.log(formdata.get('userName'))
      this.itmSvc.postItem(formdata)
                  .then((result) => {
                      // console.log(result);
                      this.router.navigate(['/category', this.addForm.get('category')?.value]);
                    })
                    .catch((err) => console.log(err))
    // console.log(formdata.get('photo'))
    
  }
  createForm(itmDescription: string): FormGroup{
    const currentDate = new Date().toISOString().substring(0, 10); // Get current date in "yyyy-MM-dd" format

    const form = this.fb.group({
      description: this.fb.control(itmDescription),
      price: this.fb.control(''),
      purchaseOn: this.fb.control(currentDate),
      timeWorn: this.fb.control(0),
      category: this.fb.control(''),
  })

  return form
}
  
  
  
}
