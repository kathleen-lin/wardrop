import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ItemService } from '../item.service';

@Component({
  selector: 'app-add-item',
  templateUrl: './add-item.component.html',
  styleUrls: ['./add-item.component.css']
})
export class AddItemComponent implements OnInit {
 
  addForm!: FormGroup 

  @ViewChild('photo')
  photoFile!: ElementRef

  // itemPhoto!: File

  // dataUrl = ""

  // imageBlob!: Blob

  constructor(private fb: FormBuilder, private httpClient: HttpClient) {}

  ngOnInit(): void {

    this.addForm = this.fb.group({
      photo: this.fb.control(''),
      description: this.fb.control(''),
      price: this.fb.control(0),
      purchaseOn: this.fb.control(''),
      timeWorn: this.fb.control(''),
      category: this.fb.control(''),

  })

  }
  
  // post the data to backend, then persist into database
  processForm() {

    // convert photo from a file to a dataUrl -> blob
    // this.itemPhoto = this.photoFile.nativeElement.files[0]
    // const reader = new FileReader();
    // reader.onload = (e: any) => {
    //   this.dataUrl = e.target.result;
    // };
    // reader.readAsDataURL(this.itemPhoto);

    // console.log(this.dataUrl)

    // this.imageBlob = this.dataURItoBlob(this.dataUrl)

    const formdata = new FormData()
    formdata.set('photo', this.photoFile.nativeElement.files[0])
    formdata.set('description', this.addForm.get('description')?.value)
    formdata.set('price', this.addForm.get('price')?.value)
    formdata.set('purchaseOn', this.addForm.get('purchaseOn')?.value)
    formdata.set('timeWorn', this.addForm.get('timeWorn')?.value)
    formdata.set('category', this.addForm.get('category')?.value)
    console.log(formdata.get('purchaseOn'))
    this.httpClient.post('http://localhost:8080/api/upload', formdata)
      .subscribe(response => console.log(response))

    // console.log(formdata.get('photo'))
      
  }
  
  dataURItoBlob(dataURI: String){
    var byteString = atob(dataURI.split(',')[1]);
    let mimeString = dataURI.split(',')[0].split(';')[0];
    var ar = new ArrayBuffer(byteString.length);
    var ai = new Uint8Array(ar);
    for (var i=0; i <byteString.length; i++){
      ai[i] = byteString.charCodeAt(i);
    }
    return new Blob([ar], {type: mimeString});
  }
}
