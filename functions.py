import sys
import os

def scan_for_trojans(trojan_list):
    # Daftar file yang terdeteksi (contoh dummy data)
    detected_files = [
        r"C:\Users\Public\Documents\malicious_file1.exe",
        r"C:\Temp\suspicious_script.vbs",
        r"C:\ProgramData\unknown_app.exe"
    ]

    # Mengirimkan hasil scan ke Java (output dipisahkan dengan ';')
    if detected_files:
        print(";".join(detected_files))
    else:
        print("No threats detected")


def enable_cmd():
    try:
        os.system('reg delete "HKCU\\Software\\Policies\\Microsoft\\Windows\\System" /v DisableCMD /f')
        print("CMD berhasil diaktifkan.")
    except Exception as e:
        print(f"Terjadi kesalahan: {e}")

def disable_cmd():
    try:
        os.system('reg add "HKCU\\Software\\Policies\\Microsoft\\Windows\\System" /v DisableCMD /t REG_DWORD /d 1 /f')
        print("CMD berhasil dinonaktifkan.")
    except Exception as e:
        print(f"Terjadi kesalahan: {e}")

def enable_task_manager():
    try:
        os.system('reg delete "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System" /v DisableTaskmgr /f')
        print("Task Manager berhasil diaktifkan.")
    except Exception as e:
        print(f"Terjadi kesalahan: {e}")

def disable_task_manager():
    try:
        os.system('reg add "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System" /v DisableTaskmgr /t REG_DWORD /d 1 /f')
        print("Task Manager berhasil dinonaktifkan.")
    except Exception as e:
        print(f"Terjadi kesalahan: {e}")

def exit():
    os._exit()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Gunakan perintah: python functions.py <command>")
        sys.exit(1)

    command = sys.argv[1]

    if command == "enable_cmd":
        enable_cmd()
    elif command == "disable_cmd":
        disable_cmd()
    elif command == "enable_task_manager":
        enable_task_manager()
    elif command == "disable_task_manager":
        disable_task_manager()
    elif command == "scan_trojans":
        scan_for_trojans()
    elif command == "exit":
        exit()
    else:
        print("Perintah tidak dikenali.")
