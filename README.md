pada RULES saya menambah ini
"rules": {
    "matakuliah": {
      ".indexOn": ["dosenId"],
      ".read": true,
      ".write": true
    },
    "nilai": {
      ".indexOn": ["mahasiswaId"],
      ".read": true,
      ".write": true
    },
}

{
  "matakuliah": {
    "TC715A_1752244999932": {
      "createdBy": "67001",
      "dosenId": "67002",
      "id": "TC715A_1752244999932",
      "kode": "TC715A",
      "nama": "Pemograman Mobile",
      "semester": 9,
      "sks": 3
    }
  },
  "nilai": {
    "672022001_TC715A_1752244999932_1752246862260": {
      "grade": "A",
      "id": "672022001_TC715A_1752244999932_1752246862260",
      "inputBy": "67002",
      "mahasiswaId": "672022001",
      "mataKuliahId": "TC715A_1752244999932",
      "nilai": 90,
      "semester": 9,
      "tahunAkademik": "2024/2025"
    }
  },
  "users": {
    "67001": {
      "email": "67001@uksw.edu",
      "id": "67001",
      "nama": "Budhi K",
      "password": "123123",
      "userType": "KAPROGDI"
    },
    "67002": {
      "email": "67002@uksw.edu",
      "id": "67002",
      "nama": "Pratama",
      "password": "123123",
      "userType": "DOSEN"
    },
    "672022001": {
      "email": "672022001@student.uksw.edu",
      "id": "672022001",
      "nama": "Prizias",
      "password": "123123",
      "userType": "MAHASISWA"
    }
  }
}