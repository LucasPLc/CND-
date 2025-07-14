
import React from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { toast } from 'sonner';

interface CNDRecord {
  id: string;
  cnpj: string;
  nomeContribuinte: string;
  tipo: 'FED' | 'EST' | 'MUN';
  orgaoEmissor: string;
  situacaoCertidao: 'Negativa' | 'Positiva com Efeitos de Negativa' | 'Positiva';
  dataEmissao: string;
  dataValidade: string;
  statusProcessamento: 'Concluído' | 'Pendente' | 'Erro' | 'Emissor Indisponível';
  dataProcessamento: string;
  codigoControle: string;
  arquivoDisponivel: boolean;
  detalhesErro?: string;
}

interface EditRecordModalProps {
  isOpen: boolean;
  onClose: () => void;
  record: CNDRecord | null;
  onSave: (record: CNDRecord) => void;
}

const EditRecordModal: React.FC<EditRecordModalProps> = ({ isOpen, onClose, record, onSave }) => {
  const [formData, setFormData] = React.useState<CNDRecord | null>(null);

  React.useEffect(() => {
    if (record) {
      setFormData({ ...record });
    }
  }, [record]);

  const handleSave = async () => {
    if (!formData) return;

    const url = record ? `/api/certidoes/${record.id}` : '/api/certidoes';
    const method = record ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Erro ao salvar registro');
      }

      const savedRecord = await response.json();
      onSave(savedRecord);
      toast.success(`Registro ${record ? 'atualizado' : 'criado'} com sucesso!`);
      onClose();
    } catch (error) {
      toast.error(error.message);
    }
  };

  if (!formData) return null;

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Editar Registro CND</DialogTitle>
        </DialogHeader>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 py-4">
          <div>
            <Label htmlFor="cnpj">CNPJ (Somente Leitura)</Label>
            <Input 
              id="cnpj"
              value={formData.cnpj} 
              disabled 
              className="bg-gray-100"
            />
          </div>
          
          <div>
            <Label htmlFor="nome">Nome do Contribuinte</Label>
            <Input 
              id="nome"
              value={formData.nomeContribuinte}
              onChange={(e) => setFormData({...formData, nomeContribuinte: e.target.value})}
            />
          </div>

          <div>
            <Label>Tipo de Certidão</Label>
            <Select 
              value={formData.tipo} 
              onValueChange={(value: 'FED' | 'EST' | 'MUN') => setFormData({...formData, tipo: value})}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="FED">Federal</SelectItem>
                <SelectItem value="EST">Estadual</SelectItem>
                <SelectItem value="MUN">Municipal</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label htmlFor="orgao">Órgão Emissor</Label>
            <Input 
              id="orgao"
              value={formData.orgaoEmissor}
              onChange={(e) => setFormData({...formData, orgaoEmissor: e.target.value})}
            />
          </div>

          <div>
            <Label>Situação da Certidão</Label>
            <Select 
              value={formData.situacaoCertidao} 
              onValueChange={(value: 'Negativa' | 'Positiva com Efeitos de Negativa' | 'Positiva') => 
                setFormData({...formData, situacaoCertidao: value})}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="Negativa">Negativa</SelectItem>
                <SelectItem value="Positiva com Efeitos de Negativa">Positiva com Efeitos de Negativa</SelectItem>
                <SelectItem value="Positiva">Positiva</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label htmlFor="dataEmissao">Data de Emissão</Label>
            <Input 
              id="dataEmissao"
              type="date"
              value={formData.dataEmissao}
              onChange={(e) => setFormData({...formData, dataEmissao: e.target.value})}
            />
          </div>

          <div>
            <Label htmlFor="dataValidade">Data de Validade</Label>
            <Input 
              id="dataValidade"
              type="date"
              value={formData.dataValidade}
              onChange={(e) => setFormData({...formData, dataValidade: e.target.value})}
            />
          </div>

          <div>
            <Label htmlFor="codigoControle">Código de Controle</Label>
            <Input 
              id="codigoControle"
              value={formData.codigoControle}
              onChange={(e) => setFormData({...formData, codigoControle: e.target.value})}
            />
          </div>
        </div>

        <div className="flex justify-end space-x-2 pt-4">
          <Button variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button onClick={handleSave} className="bg-primary hover:bg-primary/90">
            Salvar
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default EditRecordModal;
