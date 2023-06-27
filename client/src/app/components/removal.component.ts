import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ItemService } from '../item.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Dialog } from '@angular/cdk/dialog';
import { DialogData } from '../model';

@Component({
  selector: 'app-removal',
  templateUrl: './removal.component.html',
  styleUrls: ['./removal.component.css']
})
export class RemovalComponent implements OnInit {

  removalForm! :FormGroup
  rr!: string

  constructor(private router: Router, private activatedRoute: ActivatedRoute,private itmSvc: ItemService, private fb: FormBuilder, private dialogRef: MatDialogRef<RemovalComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData ){}

  ngOnInit(): void {
      this.removalForm = this.fb.group({
        removalReason: this.fb.control<any>('')
      })
      
    }

  close() {
    this.dialogRef.close()
  }

  processRemoval() {
    // console.log(this.data.itemId)
    // console.log(this.data.reason)
    // do a delete mapping while passing in reason
    this.itmSvc.removeItem(this.data.itemId, this.data.reason)
    .then((result) => console.log(result))
    .catch((err) => console.log(err))
    this.router.navigate(["/home"])
    this.close()
    
  }











}
